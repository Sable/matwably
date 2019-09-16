function [a] = get2(a)
	A = [1,2,3,4,5]
	disp(A)
	i = 3
	c = randi(5,1,1)
	disp(c)
	a = A(c)
	disp(a)
%	Random shape single access
    A = ones(randi(5,1,1),3)
    disp(size(A))
%    Should add bound check here
    d = A(c)
    disp(d)

end
