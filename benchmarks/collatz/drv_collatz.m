function drv_collatz(scale)
    max_length = 0;
    max_num = 0;
    tic();
    for i = 1:scale
        length = collatz(i);
        if length > max_length
            max_length = length;
            max_num = i;
        end
    end
    t = toc();
    disp(t);
end
