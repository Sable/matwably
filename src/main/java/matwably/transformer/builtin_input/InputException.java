/*
 *  Copyright (c) 2018. David Fernando Herrera, McGill University
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 *  OF ANY KIND, either express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 */

package matwably.transformer.builtin_input;

class InputException extends RuntimeException{
    public InputException(String s) {
        super(s);
    }

}
class TooManyInputsExpection extends InputException {

    public TooManyInputsExpection(String functionName,int numberOfInputs) {
        super("Function: "+ functionName+
                ", expected: "+ numberOfInputs+" inputs");
    }
}