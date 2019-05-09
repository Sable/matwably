function arg1(a)
   [a,b,c] = arg1_helper(ones(2,2),21,rand(2),colon(2,3))
    % Since no return values, a,c should be freed.
end