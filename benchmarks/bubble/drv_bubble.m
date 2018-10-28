function drv_bubble(size)
A= rand(300,1);
A=10000*A;
tic();
for i=1:size
y=bubble(A);
end
t = toc();
disp(t);
end
