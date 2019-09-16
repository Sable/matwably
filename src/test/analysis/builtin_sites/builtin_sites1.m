function builtin_sites1(s)
    a = ones(2,2)
    b = ones(2,2)
    d = ones(2,3)
    e = ones(1,3)
    for i=1:1
        c = a + b
        g = a - b
        h = c .* c
        k = a ./ b
        f = e + d
        disp(c)
        disp(g)
        disp(h)
        disp(k)
        disp(f)
    end
end