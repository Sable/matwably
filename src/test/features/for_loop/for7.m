function [a] = for7(b)
% FOR LOOP 1 INCREASING NO STEP
s = 10;
H = zeros(s);
for c = 1:s
for r = 1:s
    disp(c)
    disp(r)
    H(r,c) = 1/(r+c-1);
end
end
disp(H)
a = 3;
end