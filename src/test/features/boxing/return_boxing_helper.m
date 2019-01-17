function [a] = return_boxing_helper()

    if rand() > 0.5
        a = ones(1,2);
    else
        a = 1;
    end

end