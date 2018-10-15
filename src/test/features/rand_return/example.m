function example()
    % MxArray of doubles
    a = [2,2]; 
    %  In memory is an MxArray: [TypeAttr,numel,data_ptr, ndims, 
    %   dim_ptr,strides, other_attr]
    
    % Scalar
    b = 2; % Stored value in local register and value is not loaded in/out
           % of memory
    
           
    c = 2; % If boxing [TypeAttr,4,->[2], 2,->[1,1],->[8,8], other_attr]
           % Total: 72 bytes and we have to load/unload from memory.
       
    e = b + 2; % Call $add_MS
    d = c + 2; % Simple scalar addition operator
end