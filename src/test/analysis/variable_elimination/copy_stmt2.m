function copy_stmt2(a)
    b = 21;
    c = b;
    while( true )
        d = 4 + c;
    end
    % Should become d = 4+c, and leave c defined. Only one level, we would need to create the transformation, and re-run.
    % The whole analysis using the rhs of the CopyStmt. Then revert, if the new variable is ambiguous.
    % Create temporary ReachingDefs analysis, if the temp ReachDefs variable is ambiguous
end