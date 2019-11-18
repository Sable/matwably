function alloc_site1(s)
    a = ones(2,2,2)
    for i=1:1000
        a = ones(2,2,2) + a
    end
    disp(a)
end