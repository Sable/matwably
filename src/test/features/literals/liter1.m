function liter1(a)
    x = [0,2;2,2]
    rows = size(x,1)
    vars = size(x,2)
%    sigma = (x(1) - 1) * rows - (x(1) - 1) * (x(1) / 2);
    y = randi(vars);
    vars = size(x, 2);
    max_rows = rows * (rows - 1) / 2
    disp(max_rows)
    sq_dev = zeros(max_rows, vars);
    new_sq_dev = zeros(max_rows, 1);
    sum_sq_dev = zeros(1, max_rows); % must be set at zeros for calculations
    sqrt_sum = zeros(1, max_rows);
    sum_criterion = 0
    % D3. copy columns and efficiency criterion
%    new_sq_dev = sq_dev(1:size(sq_dev, 1), y);
    new_sum_sq_dev = sum_sq_dev;
    new_sqrt_sum = sqrt_sum;
%    new_sum_criterion = sum_criterion;
%    disp(3)
%    i = x(1)
%    disp(ndims(sigma))

end