function return1(e)
    [a,b,c] = return1_helper(e);
    disp(return1_helper(e)); % Should print the first value.
    [a,b] = return1_helper(e); % Should print both values.
    disp(a)
    disp(b)
end
