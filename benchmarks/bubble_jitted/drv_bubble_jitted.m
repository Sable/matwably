function drv_bubble(size)
A= rand(300,1);
A=10000*A;
// Warm-up iters
for i=1:5
y=bubble(A);
end
// Steady state iters
tic();
for i=1:size
y=bubble(A);
end
t = toc();
disp(t);
end
