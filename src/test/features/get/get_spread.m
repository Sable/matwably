function  get_spread(a)
    b = zeros(5,5)
    x = randn(1,5)
    b(:,1) = 1;
    b(:,2) = x';
    disp(b)
end