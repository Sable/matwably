function [g] = set_index(g)
%    Known bound, known shape
    b = randn(2,2,3)
    b(2,2,3) = randi(10,1,1);
    disp(b(2,2,3))
    b(12) = 12
    disp(b(12))
%    Unknown bound, known shape
    size = randi(3,1,1)
    b = randn(2,2,3)
    b(size) = 20
    disp(b(size))
    b(2,2,size) = 20
    disp(b(2,2,size))
%    known bound, unknown shape
    size = randi(3,1,1)
    b = randn(2,2,size+1)
    b(2,2,1) = 20
    disp(b(2,2,1))
%   unknown bound, unknown shape
    b = randn(2,2,size+1)
    s = randi(1,1,1)
    b(2,2,s) = 20
    disp(b(2,2,1))
end