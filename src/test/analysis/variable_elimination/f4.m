function t = f4(a)
    tic()
    for i = 0:10000
        c = 3 + 20 - 5 * 20;
    end
    t = toc()
    disp(t)
end
