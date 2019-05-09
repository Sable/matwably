function size_builtin(a)
    a = size(ones(3,3));
    disp(a)
    [b,a] = size(ones(4,4));
    disp(b);
    disp(a);
    [c,d] = size(ones(5,4,3));
    disp(c)
    disp(d)
    [a,d,c] = size(ones(5,4,3));
    disp(a)
    disp(d)
    disp(c)
    [c,d,e,h] = size(ones(5,4,3));
    disp(c)
    disp(d)
    disp(e)
    disp(h)
    size(ones(2,2));
    [c,d,e,h] = size(3);
    disp(c)
    disp(d)
    disp(e)
    disp(h)
end