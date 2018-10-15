function boxing(a)
  if rand(1,1) > 0.5
    h = 3;
  else
    h = eye(2,2);
  end
  disp(h);
  for i=1:h:10
    disp(i)
  end
end