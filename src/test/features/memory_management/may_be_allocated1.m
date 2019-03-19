function a = may_be_allocated1(c)
    if c < 10
       a = ones(3,3);
%        since we don't know, in this case, we add it to set of dynamic
%        checks. 
    else
        disp(33);
    end
    
    a = 23;
    % Since a, may or may not point to a particular place, we 
end