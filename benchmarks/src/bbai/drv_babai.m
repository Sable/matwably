function drv_babai(size)
	R = randn(size);
	Y = randn(size, 1);
	tic();
    [vec] = babai(R, Y);
	t = toc();
  disp(t);
	
end