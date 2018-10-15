function drv_prime( scale )
%DRV_PRIME Summary of this function goes here
%   Detailed explanation goes here
%tic
tic();
y = prime_total(scale);
t = toc();
disp(t);
%toc
end

