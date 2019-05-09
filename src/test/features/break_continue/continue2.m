function continue2(s)
    for i=1:10
        for j=2:10
            if j == 3
                continue;
            end
            disp(j)
        end
        if i == 2
            continue;
        end
        disp(i)
    end
end