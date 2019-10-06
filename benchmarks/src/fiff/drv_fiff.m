function drv_fiff(scale)
%%
%% Driver for finite-difference solution to the wave equation.
%%

a=2.5;
b=1.5;
c=0.5;
n=1750;
m=1750;
tic();
for time=1:scale
  U=finediff(a, b, c, n, m);
end
t = toc();
disp(t)
end

