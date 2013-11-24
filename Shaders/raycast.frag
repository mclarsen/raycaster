#version 330                                                                        
                                                                                    




uniform sampler2D backFace;
uniform sampler3D volume;     
uniform sampler1D transferFunction;                                                  
uniform float stepSize;
uniform vec2 thresholds;
uniform vec3 color;
uniform float red;


in vec4 varyingColor;//this is where the ray penetrates the volume
in vec4 varyingVert;   
out vec4 fragColor;                                                                  


void main()                                                                         
{   float step=.002;

	
	
	
	//calculate the geometry
	vec2 backCoord=((varyingVert.xy/varyingVert.w+1)/2);//get the screenspace coordinates to lookup the right backface position
	vec4 outLoc=texture(backFace,backCoord);
	vec3 rayDir=outLoc.xyz-varyingColor.xyz;
	
	float rayLength=length(rayDir.xyz);
	rayDir=normalize(rayDir);
	
	vec3 rayStep=step*rayDir;

	float stepLength=length(rayStep);
	vec3 currentPosition=varyingColor.xyz;
	float currentLength=0;
	vec4 currentColor=vec4(0,0,0,0);
	vec4 sampleColor= vec4(0,0,0,0);
	float scalar;

	for(int i=0; i<1000;i++){
		scalar=texture(volume, currentPosition.xyz).r;
		sampleColor.r=color.r/1000;
		sampleColor.g=color.g/1000;
		sampleColor.b=color.b/1000;
		if(scalar>thresholds.x && scalar <thresholds.y) scalar=scalar;
		else scalar=0;
		
		sampleColor.rgba=texture(transferFunction,scalar).rgba;
		
		//sampleColor.a=scalar;
		currentColor.rgb+=((1-currentColor.a)*sampleColor.rgb*sampleColor.a)*3;
		currentColor.a+=((1-currentColor.a)*sampleColor.a)*step*20;   //make sure we don't take the full alpha
		
		//advance then check for termination
		currentPosition+=rayStep;
		currentLength+=stepLength;
		if(currentLength>=rayLength ){
			break;
		}
		
		//if(currentColor.a>=thresholds.y) {
		//	//currentColor.a=.9;
		//	break;
		//}

	}
	fragColor=currentColor;
                                                                         
}