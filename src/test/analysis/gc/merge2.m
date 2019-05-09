function merge2(a)
    b = ones(3,3)
    c = b
    if a < rand()
%        b = ones(3,3)
        d = c
    else
%        b = ones(2,2)
        e = c
    end
    e = 3 % decrease statically C count
    d = 5 % decrease statically C count
    c = 3 %decrease statically C count
    d = 4 % decrease statically C count
    % c's reference count remains the same, therefore the variable remains static.
    %
    return
end