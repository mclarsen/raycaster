#version 330                                                                        
                                                                                    




uniform sampler2D backFace;
uniform sampler3D volume;                                                     
uniform float stepSize;
uniform vec2 thresholds;


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
		sampleColor.r=255;
		sampleColor.g=102;
		sampleColor.b=0;
		if(scalar>thresholds.x && scalar <thresholds.y) scalar=scalar;
		else scalar=0;
		sampleColor.a=scalar;
		currentColor.rgb+=(1-currentColor.a)*sampleColor.rgb*3;
		currentColor.a+=(1-currentColor.a)*sampleColor.a*step*50;   //make sure we don't take the full alpha
		
		//advance then check for termination
		currentPosition+=rayStep;
		currentLength+=stepLength;
		if(currentLength>=rayLength ){
			break;
		}
		
		if(currentColor.a>=1) {
			currentColor.a=1;
			break;
		}

	}
	fragColor=currentColor;
                                                                         
}