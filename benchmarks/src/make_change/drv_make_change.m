function drv_make_change(scale)
    coins = [1 5 10 25];
    tic();
    for i = 0:scale
        make_change(coins, 4, i);
    end
t = toc();
disp(t);
end
