function return2(e)
    disp(return1_helper(e)); % Should print the first value. Even if other two return variables of function 'return1_helper'
                             % are not defined.
end