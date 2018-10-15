function [a] = break2(b)

% FOR LOOP 2 Decreasing
for v = 1.0:-0.2:0.0
  disp(v)
  if v == 0.2
    break;
  end
end
a = 3;
end