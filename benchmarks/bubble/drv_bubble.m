function drv_bubble(size)
A= rand(size,1);
A=10000*A;
tic();
for i=1:10
y=bubble(A);
end
t = toc();
disp(t);
end
