function drv_matmul_p(scale)

%m = arr(1);
%k = arr(2);
%n = arr(3);
scale = 100;
m=scale;
k=scale/2;
n=scale;

A = 100*randn(m,k);
B = 100*randn(k,n);
tic();
C = matmul_p(A,B,m,k,n);
t = toc();
disp(t)

end

