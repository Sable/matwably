function drv_babai(size)
	R = randn(size);
	Y = randn(size, 1);
	tic();
    for i = 1:size
	[vec] = babai(R, Y);
    end
	t = toc();
  disp(t);
	
end