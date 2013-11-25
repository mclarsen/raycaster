#version 330                                                                        



                                                                              
struct PositionalLight{
  vec4 ambient;
  vec4 diffuse;
  vec4 specular;
  vec3 position;
};
uniform PositionalLight light; 
uniform vec4 globalAmbient;  

uniform sampler2D backFace;
uniform sampler3D volume;     
uniform sampler1D transferFunction;                                                  
uniform float stepSize;
uniform vec2 thresholds;
uniform vec3 color;
uniform mat4 projMatrix;// we need this to get eye space vertex locations
uniform vec3 camPos;

in vec4 varyingColor;//this is where the ray penetrates the volume
in vec4 varyingVert;   
out vec4 fragColor;                                                                  

//all these params need eye space.
vec4 getPhongColor(vec3 lightDir, vec3 vertex, vec3 normal){
	vec4 phongColor= vec4(0,0,0,0);
	vec4 lightAmb= light.ambient;
	vec4 lightDiff= light.diffuse;
	vec4 lightSpec= light.specular;
	
	vec4 matAmb=vec4(.5,.5,.5,1);
	vec4 matDiff=vec4(.5,.5,.5,1);
	vec4 matSpec=vec4(.9,.9,.9,1);
	float matShininess=.4;
	
	//compute normailized lights, normal, and eye dir vecs
	vec3 L = normalize(lightDir);
	vec3 N = normalize(normal);
	vec3 V = normalize( vec3(0,0,0)- normalize(vertex) );
	
	//get angle between light and normal surface
	float cosTheta= dot(L,N);
	
	//reflect light vec 
	vec3 R = normalize( reflect(-L,N) );
	
	//compute angle between the vector to the eye and reflected light dir
	float cosPhi = dot (V,R);
	
	//compute reflected color
	phongColor= globalAmbient * matAmb + lightAmb*matAmb
			 	+ lightDiff * matDiff * max( cosTheta, 0.0 )
			 	+ lightSpec*matSpec * pow ( max(cosPhi, 0.0), matShininess); 
	
	
	return phongColor;
}


void main()                                                                         
{   float step=.002;

	
	mat4 invProjMat= inverse(projMatrix);
	
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
	
	const ivec3 off= ivec3(-1,0,1);
	
	float deltaX;
	float deltaY;
	float deltaZ;
	vec4 lightColor= vec4(0,0,0,0);
	vec4 vertexEyeSpace=vec4(0,0,0,0);
	
	for(int i=0; i<1000;i++){
		scalar=texture(volume, currentPosition.xyz).r;
		sampleColor.r=color.r/1000;
		sampleColor.g=color.g/1000;
		sampleColor.b=color.b/1000;
		if(scalar>thresholds.x && scalar <thresholds.y) scalar=scalar;
		else scalar=0;
		
		sampleColor.rgba=texture(transferFunction,scalar).rgba;
		
		//deltaX=(textureOffset(volume,currentPosition.xyz, off.zyy).r-textureOffset(volume,currentPosition.xyz, off.xyy).r)/2.0;
		//deltaY=(textureOffset(volume,currentPosition.xyz, off.yzy).r-textureOffset(volume,currentPosition.xyz, off.yxy).r)/2.0;
		//deltaZ=(textureOffset(volume,currentPosition.xyz, off.yyz).r-textureOffset(volume,currentPosition.xyz, off.yyx).r)/2.0;
		//vertexEyeSpace=invProjMat*vec4(currentPosition,1);
		//lightColor=getPhongColor(light.position.xyz-vertexEyeSpace.xyz,vertexEyeSpace.xyz, vec3(deltaX,deltaY,deltaZ));
		
		//sampleColor.a=scalar;
		//currentColor.rgb+=((1-currentColor.a)*sampleColor.rgb*lightColor.rgb);
		currentColor.rgb+=((1-currentColor.a)*sampleColor.rgb);
		currentColor.a+=((1-currentColor.a)*sampleColor.a);   //make sure we don't take the full alpha
		//if(sampleColor.a>.9) {
		//	currentColor.a=1;
		//	break;
		//}
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

