function [b] = set(size)
    b = randn(3,7,2)
    disp(b)
    b(:) = 2
    disp(b)
    b(:,2,2) = [1,2,3]
    disp(b(:,2,2))
    b(1:end,1,1) = 3
    disp(b)
end
