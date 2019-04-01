function [a] = binary_operators(d)

% SS
a = 30
b = 10
disp(a+b)
disp(a-b)
disp(a./b)
disp(a.*b)
disp(a.^b)
disp(a.\b)
%
%%% MM samesize
a = ones(3,3)
b = ones(3,3)
disp(a+b)
disp(a-b)
disp(a./b)
disp(a.*b)
disp(a.^b)
disp(a.\b)
%
%% MM broadcasting
a = ones(3,3)
b = ones(1,3)
disp(a+b)
disp(a-b)
disp(a./b)
disp(a.*b)
disp(a.^b)
disp(a.\b)
%%
% SM
a = 3
b = ones(3,3)
disp(a+b)
disp(a-b)
disp(a./b)
disp(a.*b)
disp(a.^b)
disp(a.\b)
%
%% MS
b = 3
a = ones(3,3)
disp(a+b)
disp(a-b)
disp(a./b)
disp(a.*b)
disp(a.^b)
disp(a.\b)

end