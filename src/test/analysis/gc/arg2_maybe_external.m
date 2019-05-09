function arg2_maybe_external(a)
    disp(a)
    % box a, then treat it
    if a >  rand()
        a = ones(3,2)
        b = a
        c = a
    end
    disp(a)
    return;
end