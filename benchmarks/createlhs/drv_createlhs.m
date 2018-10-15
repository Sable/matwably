function drv_createlhs(scale)
%DRV_CREATELHS Summary of this function goes here
%   Detailed explanation goes here
tic();
for i=1:2:scale
    arr=randn(4,4)*10;
    sum_criterion=createlhs(arr);
end
t = toc();
disp(t);
end

