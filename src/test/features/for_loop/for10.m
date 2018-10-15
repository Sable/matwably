function [a] = for10(b)
  N = 4;
  A = zeros(N);
  for ii=1:N,
    for jj=1:N,
      if ii*jj<N/2,
        A(N-ii, ii+jj)=1;
        A(ii, N-ii-jj)=1;
        
      end;
      if (ii==jj),
        A(ii, jj)=1;
      end;
    end;
  end;
  B=A;
    
    % Perform actual work.
    %Rename ii1 to ii once VarRenameOnTypeCpnflict is Fixed
    ii1=N/2;
    while ii1>=1
          B=B*B;
          disp(B);
          ii1=ii1/2;
    end;
a = 3;
end