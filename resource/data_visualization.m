function [  ] = data_visualization( draw_method, filename )
%DATA_VISUALIZATION Summary of this function goes here
%   Detailed explanation goes here

x = load(filename);
[pc, score, latent, tsquare] = princomp(x);

if strcmp(draw_method,'2D')
    scatter(pc(:,1),pc(:,2));
elseif strcmp(draw_method,'3D')
    scatter3(pc(:,1),pc(:,2),pc(:,3));
elseif strcmp(draw_method,'paracoord')
    parallelcoords(x)
end

