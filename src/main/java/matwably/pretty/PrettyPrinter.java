/*
 * Copyright (c) 2018. David Herrera
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package matwably.pretty;

import matwably.ast.*;

public class PrettyPrinter implements ASTConcreteNodeVisitor {

    private StringBuffer sb;
    private Module module;
    private int indentation_level = 0;
    private boolean new_line = true;
    public PrettyPrinter(Module mod) {
        this.module = mod;
    }

    public void visit(Module mod) {
        sb.append("(module ");
        if( mod.hasIdentifier() ) {
            prettyIdentifier(mod.getIdentifier());

        }
        sb.append("\n");
        indentation_level ++;
        if( mod.hasImport() ) {
            mod.getImportList().forEach((Import imp)->imp.accept(this));
        }
        if( mod.hasStart() ) {
            mod.getStart().accept(this);
        }
        if( mod.hasImportedWat() ) {
            mod.getImportedWatList().forEach((ImportedWat imp)->{sb.append(imp.getDecl());});
        }
        if(mod.hasMemory())
        {
            mod.getMemoryList().forEach((Memory mem)->{
                mem.accept(this);
            });
        }
        if(mod.hasType())
        {
            mod.getTypeList().forEach((Type type)->{
                type.accept(this);
            });
        }
        if ( mod.hasFunctions() ){
            mod.getFunctionsList().forEach((Function func )->{
                func.accept(this);
            });
        }
        if ( mod.hasDataSegment() ) {
            mod.getDataSegmentList().forEach((DataSegment data_seg )->{
                data_seg.accept(this);
            });
        }
        if (mod.hasGlobal()){
            mod.getGlobalList().forEach((Global glob)->glob.accept(this));
        }

        if(mod.hasElementSegment())
        {
            mod.getElementSegmentList().forEach((ElementSegment elem)->elem.accept(this));
        }

        if( mod.hasExport() ) {
            mod.getExportList().forEach((Export exp)->exp.accept(this));
        }
        indentation_level--;
        sb.append(")\n");
    }
    public String display() {
        this.sb = new StringBuffer();
        this.visit(this.module);
        return sb.toString();
    }
    private void indent() {
        for(int i = 0;i < indentation_level;i++) sb.append("\t");
    }
    private void prettyIdentifier(Identifier id) {
        sb.append("$");
        sb.append(id.getName());
    }
    public void visit(TypeUse use) {
        if(use.hasIdentifier()) prettyIdentifier(use.getIdentifier());
        sb.append(" ");
        use.getType().accept(this);
    }
    public void prettyFunctionType(Identifier id,List<TypeUse> parameters, ReturnType ret, boolean printParamId){
        sb.append("(func ");
        if(id != null) prettyIdentifier(id);
        sb.append(" ");
        prettyParameterList(parameters, printParamId);
        if(ret != null) prettyReturnType(ret);
    }
    public void visit(Function func) {
        indent();
        prettyFunctionType((func.hasIdentifier())?func.getIdentifier():null,
                func.getType().getParameters(), func.getType().getReturnType(), true);
        sb.append("\n");
        indentation_level ++;
        prettyLocalsList(func.getLocalsList(), true);
        sb.append("\n");
        Expression exp = func.getBody();
        if( exp != null && exp.hasInstructions() ) {
            printInstructionList(exp.getInstructionsList());
        }
        indentation_level --;
        indent();
        sb.append(")\n");
    }


    public void visit(ValueType val){
        sb.append(val.getTypeString());
    }
    public void visit(Table tab) {
        sb.append("(table ");
        if (tab.hasIdentifier()) {
            prettyIdentifier(tab.getIdentifier());
        }
        TableType tabtype = tab.getTableType();
        Limit lim = tabtype.getLimits();

        sb.append(lim.getMin());
        sb.append(" ");
        if ( lim.hasMax() )sb.append(lim.getMax().getValue());
        sb.append(" ");
        sb.append(tabtype.getElemType());
        sb.append(")");
    }
    public void visit(Global global) {
        sb.append("(global ");
        if( global.hasIdentifier()) prettyIdentifier(global.getIdentifier());
        prettyGlobalType(global.getGlobalType());
        sb.append(" ");
        new_line = false;
        visit(global.getInit());
        new_line = true;
        sb.append(")");
    }
    public void visit(ElementSegment element_segment) {
        sb.append("(elem ");
        Idx idx = element_segment.getTableIndex();
        if( idx.hasIdentifier()) prettyIdentifier(idx.getIdentifier());
        else sb.append(idx.getIndex());
        sb.append(" ( ");
        new_line = false;
        element_segment.getOffset().accept( this );
        new_line = true;
        sb.append(") ");
        // TODO: Take care of initial ConstantExpression
    }
    public void visit(Memory mem) {
        indent();
        sb.append("(memory ");
        if ( mem.hasIdentifier() ) prettyIdentifier(mem.getIdentifier());
        sb.append(" ");
        prettyLimits(mem.getLimits());
        sb.append(" )\n");
    }
    public void prettyLimits(Limit lim){
        sb.append(lim.getMin());
        if ( lim.hasMax() )  sb.append(" " + lim.getMax().getValue());
    }
    public void visit(DataSegment data_seg) {
        indent();
        sb.append("(data ");
        printIndex(data_seg.getMemoryIndex());
        sb.append(" ( ");
        new_line = false;
        data_seg.getOffset().accept( this );
        new_line = true;
        sb.append(" ) \"");
        sb.append(data_seg.getInit());
        sb.append("\" )\n");
    }
    public void visit(ConstLiteral const_lit)
    {
        sb.append(const_lit.getType().getTypeString());
        sb.append(".const ");
        sb.append(const_lit.getValue());
    }

    @Override
    public void visit(GetLocal inst) {
        sb.append("get_local ");
        printIndex(inst.getIdx());
    }

    @Override
    public void visit(SetLocal inst) {
        sb.append("set_local ");
        printIndex(inst.getIdx());
    }

    @Override
    public void visit(TeeLocal inst) {
        sb.append("tee_local ");
        printIndex(inst.getIdx());
    }

    @Override
    public void visit(Clz inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".clz");
    }

    @Override
    public void visit(Ctz inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".ctz");
    }

    @Override
    public void visit(Popcnt inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".popcnt");
    }

    @Override
    public void visit(Add inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".add");
    }

    @Override
    public void visit(Sub inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".sub");
    }

    @Override
    public void visit(Mul inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".mul");
    }

    @Override
    public void visit(Div inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".div");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
    }

    @Override
    public void visit(Rem inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".rem_");
        sb.append((inst.getSigned())?"s":"u");
    }

    @Override
    public void visit(And inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".and");
    }

    @Override
    public void visit(Or inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".or");
    }

    @Override
    public void visit(Xor inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".xor");
    }

    @Override
    public void visit(Shr inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".shr");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
    }

    @Override
    public void visit(Shl inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".shl");
    }

    @Override
    public void visit(Rotl inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".rotl");
    }

    @Override
    public void visit(Rotr inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".rotr");
    }

    @Override
    public void visit(Eqz inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".eqz");
    }

    @Override
    public void visit(Eq inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".eq");
    }

    @Override
    public void visit(Ne inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".ne");
    }

    @Override
    public void visit(Le inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".le");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
    }

    @Override
    public void visit(Lt inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".lt");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
    }

    @Override
    public void visit(Gt inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".gt");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
    }

    @Override
    public void visit(Ge inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".ge");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
    }

    @Override
    public void visit(Abs inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".abs");
    }

    @Override
    public void visit(Neg inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".neg");
    }

    @Override
    public void visit(Sqrt inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".sqrt");
    }

    @Override
    public void visit(Ceil inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".ceil");
    }

    @Override
    public void visit(Floor inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".floot");
    }

    @Override
    public void visit(Trunc inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".trunc");
    }

    @Override
    public void visit(Nearest inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".nearest");
    }

    @Override
    public void visit(Max inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".max");
    }

    @Override
    public void visit(Min inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".min");
    }

    @Override
    public void visit(CopySign inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".copysign");
    }

    @Override
    public void visit(CvTrunc inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".trunc");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s/":"_u/",
                (FloatValueType int_val)->  ""));
        sb.append(inst.getFromType().getTypeString());
    }

    @Override
    public void visit(Demote inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".demote/");
        sb.append(inst.getFromType().toString());
    }

    @Override
    public void visit(Promote inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".promote/");
        sb.append(inst.getFromType().toString());
    }

    @Override
    public void visit(Reinterpret inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".reinterpret");
        sb.append(inst.getFromType().toString());
    }

    @Override
    public void visit(Convert inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".convert");
        sb.append(inst.getFromType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s/":"_u/",
                (FloatValueType int_val)-> ""));
        sb.append(inst.getFromType().getTypeString());
    }

    @Override
    public void visit(Extend inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".extend");
        sb.append(inst.getType().match_number_type(
                (IntValueType int_val)-> (inst.getSigned())?"_s":"_u",
                (FloatValueType int_val)->  ""));
        sb.append(inst.getFromType().toString());
    }

    @Override
    public void visit(Wrap inst) {
        sb.append(inst.getType().getTypeString());
        sb.append(".wrap/");
        sb.append(inst.getFromType().toString());
    }

    @Override
    public void visit(BrTable inst) {
        sb.append("br_table ");
        inst.getLabelsList().forEach((Idx idx)->{
            printIndex(idx);
            sb.append(" ");
        });

    }

    @Override
    public void visit(Select inst) {
        sb.append("select ");
    }

    @Override
    public void visit(Drop inst) {
        sb.append("drop ");
    }

    @Override
    public void visit(Load inst) {

    }

    @Override
    public void visit(Store inst) {

    }

    @Override
    public void visit(SetGlobal inst) {
        sb.append("set_global ");
        printIndex(inst.getIdx());
    }

    @Override
    public void visit(GetGlobal inst) {
        sb.append("get_global ");
        printIndex(inst.getIdx());
    }

    @Override
    public void visit(Nop inst) {
        sb.append("nop");
    }

    @Override
    public void visit(Br inst) {
        sb.append("br ");
        printIndex(inst.getLabel());
    }

    @Override
    public void visit(BrIf inst) {
        sb.append("br_if ");
        printIndex(inst.getLabel());
    }
    @Override
    public void visit(MemGrow inst) {
        sb.append("memory.grow ");
    }

    @Override
    public void visit(MemSize inst) {
        sb.append("memory.size ");
    }


    @Override
    public void visit(Call inst) {
        sb.append("call ");
        printIndex(inst.getLabel());
    }

    @Override
    public void visit(CallIndirect inst) {
        sb.append("call_indirect ");
        printIndex(inst.getTypeIdx());
    }

    @Override
    public void visit(Return inst) {
        sb.append("return");
    }

    @Override
    public void visit(Unreachable inst) {
        sb.append("unreachable");
    }

    public void prettyReturnType(ReturnType ret){
        sb.append("(result");
        ret.getTypes().forEach((ValueType type) -> {
            sb.append(" ");
            sb.append(type.getTypeString());
        });
        sb.append(")");
    }

    @Override
    public void visit(If inst) {
        sb.append("if ");
        if (inst.hasReturnType()) {
            prettyReturnType(inst.getReturnType());
        }
        sb.append("\n");
        indentation_level++;
        if(inst.hasInstructionsIf()) printInstructionList(inst.getInstructionsIfList());
        indentation_level--;
        if (inst.hasInstructionsElse()) {
            indent();
            sb.append("else\n");
            indentation_level++;
            printInstructionList(inst.getInstructionsElseList());
            indentation_level--;
        }
        indent();
        sb.append("end\n");
    }

    @Override
    public void visit(Block inst) {
        sb.append("block ");
        if(inst.hasLabel()){
            printIndex(inst.getLabel());
        }
        sb.append("\n");
        indentation_level++;
        printInstructionList(inst.getInstructionList());
        indentation_level--;
        indent();
        sb.append("end\n");
    }

    @Override
    public void visit(Loop inst) {
        sb.append("loop ");
        if(inst.hasLabel()){
            printIndex(inst.getLabel());
        }
        sb.append("\n");
        indentation_level++;
        printInstructionList(inst.getInstructionList());
        indentation_level--;
        indent();
        sb.append("end\n");
    }

    @Override
    public void visit(ImportedWat inst) {
        sb.append(inst.getDecl());
    }

    @Override
    public void visit(Export exp) {
        indent();
        sb.append("(export \"");
        sb.append(exp.getName());
        sb.append("\" ");
        exp.getDesc().accept(this);
        sb.append(" )\n");
    }
    public void visit(TableExportDesc exp){
        sb.append("(table ");
        printIndex(exp.getIdx());
        sb.append(" )");
    }
    public void visit(FunctionExportDesc exp){
        sb.append("(func ");
        printIndex(exp.getIdx());
        sb.append(" )");
    }
    public void visit(DataExportDesc exp){
        sb.append("(data ");
        printIndex(exp.getIdx());
        sb.append(" )");
    }
    public void visit(MemoryExportDesc exp){
        sb.append("(memory ");
        printIndex(exp.getIdx());
        sb.append(" )");
    }
    public void visit(GlobalExportDesc exp){
        sb.append("(global ");
        printIndex(exp.getIdx());
        sb.append(" )");
    }

    public void prettyGlobalType(GlobalType globalType) {
        if (globalType.getMut())
        {
            sb.append("(mut ");
            sb.append(globalType.getType().getTypeString());
            sb.append(")");
        }else {
            sb.append(" ");
            sb.append(globalType.getType().getTypeString());
        }
    }
    @Override
    public void visit(Import imp) {
        indent();
        sb.append("(import \"");
        sb.append(imp.getModuleName());
        sb.append("\" \"");
        sb.append(imp.getName());
        sb.append("\" ");
        DescType type = imp.getDesc().getDescType();
        type.match((GlobalType glob)->{
            sb.append("(global ");
            if(imp.getDesc().hasIdentifier()) prettyIdentifier(imp.getDesc().getIdentifier());
            prettyGlobalType(glob);
            return null;
        },(MemoryType mem)->{
            sb.append("(memory ");
            if(imp.getDesc().hasIdentifier()) prettyIdentifier(imp.getDesc().getIdentifier());
            sb.append(" ");
            prettyLimits(mem.getLimits());
            return null;

        },(FuncType func)->{
            prettyFunctionType((imp.getDesc().hasIdentifier())?imp.getDesc().getIdentifier():null,
                    func.getType().getParameters(), func.getType().getReturnType(), false);
            return null;
        },(TableType tab)->{
            sb.append("(table ");
            if(imp.getDesc().hasIdentifier()) prettyIdentifier(imp.getDesc().getIdentifier());
            sb.append(" ");
            prettyLimits(tab.getLimits());
            sb.append(" ");
            sb.append("anyfunc");
            return null;
        });
        sb.append(")");
        sb.append(")\n");
    }

    @Override
    public void visit(Start start) {
        indent();
        sb.append("(start ");
        if(start.getFunctionIndex().hasIdentifier()) prettyIdentifier(start.getFunctionIndex().getIdentifier());
        else sb.append(start.getFunctionIndex().getIndex());
        sb.append(")");
    }
    public void visit(Type type) {
        indent();
        sb.append("(type ");
        if(type.hasIdentifier()){
            prettyIdentifier(type.getIdentifier());
        }
        sb.append(" (func ");
        prettyParameterList(type.getParameters(), false);
        if( type.getParameters().getNumChild()>0) sb.append(" ");
        if(type.hasReturnType()) prettyReturnType(type.getReturnType());
        sb.append("))\n");
    }

    @Override
    public void visit(ConstantExpression const_exp) {
        const_exp.getInstructionsList().forEach((Instruction inst)->{
            if( new_line ) indent();
            inst.accept(this);
            if( new_line )sb.append("\n");
        });
    }
    private void printIndex(Idx index) {
        if ( index.hasIdentifier() ) prettyIdentifier(index.getIdentifier());
        else sb.append(index.getIndex());
    }
    private void prettyLocalsList(List<TypeUse> list, boolean printId)
    {
        indent();
        if( printId ){
            list.forEach((TypeUse t)->{
                sb.append("(local ");
                if(printId && t.hasIdentifier()) {
                    prettyIdentifier(t.getIdentifier());
                    sb.append(" ");
                }
                t.getType().accept(this);
                sb.append(")");
            });
        }else{
            sb.append("(local");
            list.forEach((TypeUse t)->{
                sb.append(" ");
                t.getType().accept(this);
                sb.append(")");
            });
        }
    }
    private void prettyParameterList(List<TypeUse> list, boolean printId)
    {
        if( printId ){
            list.forEach((TypeUse t)->{
                sb.append("(param ");
                if(printId && t.hasIdentifier()) {
                    prettyIdentifier(t.getIdentifier());
                    sb.append(" ");
                }
                t.getType().accept(this);
                sb.append(")");
            });
        }else{
            sb.append("(param");
            list.forEach((TypeUse t)->{
                sb.append(" ");
                t.getType().accept(this);
                sb.append(")");
            });
        }
    }
    private void printInstructionList(List<Instruction> list_ints)
    {
        list_ints.forEach((Instruction inst)-> {
            indent();
            inst.accept(this);
            sb.append("\n");
        });
    }

    @Override
    public String toString() {
        this.sb = new StringBuffer();
        this.visit(module);
        return sb.toString();
    }

}