function merge3(a)
    b = ones(2,2)
    c = b
    % b site is static and has reference count of 2.
    while(a<rand())
        disp(b)
        b = ones(3,3)
        % b site static reference count goes to 1 and
        % b site here is static, and c site is static here
        disp(c)
    end
    return;
    % during the merge c becomes dynamic
end