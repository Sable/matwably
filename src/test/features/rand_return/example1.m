function [d,i] = example1(e) % Entry function specified 
                              % with scalar parameter
    i = 1;
    j = 2;
    k = 10;
    
    %loop generation could be dynamic if bounds not known statically
    for v=i:j:k
        if v == 3
           continue; % Label to break out of must be known in wasm. 
        end
        disp(v)
    end
    
   
    d = ones(e,e); % Requires transformation into canonical.
    d = ones([e]); % WebAssembly constraints number of parameters.
                   % Transformation into canonical form is necessary.
                   
    e = horzcat(i,j,k); % Takes variable number of arguments. Different 
                        % Input variable handling
    
    f = j + k; % Addition of scalars
    f = j + e; % Addition of scalars + matrix (broadcasting).
    f = e+e; % Addition of two matrices
    
   
    k = eye(3,3); % Analysis results in two locals k_i32, k_f64.
    
    return; % Since d is a matrix, to return, we must box i for return.
            % Wasm currently only supports one return value
end