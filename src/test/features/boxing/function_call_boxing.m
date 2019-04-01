function function_call_boxing(a)
  if rand() > 0.5
    h = 5;
  else
    h = eye(3,3);
  end
  function_call_boxing_helper(h);

end