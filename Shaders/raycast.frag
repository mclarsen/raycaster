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
uniform mat4 normalMat;//think I need this to transform the normals into eye space
uniform mat4 modelViewMatrix;
uniform vec3 camPos;

in vec4 varyingColor;//this is where the ray penetrates the volume
in vec4 varyingVert;   
out vec4 fragColor;                                                                  

float cerp(float p0, float p1, float p2, float p3, float x){
	return p1+0.5*x*(p2-p0 +x*(2.0*p0-5.0*p1+4.0*p2-p3+x*(3.0*(p1-p2)+p3-p0)));
}


//all these params need eye space.
vec4 getPhongColor(in vec3 lightDir,in vec3 vertex,in vec3 normal){
	vec4 phongColor= vec4(0,0,0,0);
	vec4 lightAmb= light.ambient;
	vec4 lightDiff= light.diffuse;
	vec4 lightSpec= light.specular;
	
	vec4 matAmb=vec4(.4,.4,.4,.4);
	vec4 matDiff=vec4(.4,.4,.4,.4);
	vec4 matSpec=vec4(.3,.3,.3,.4);
	//perl
	//vec4 matAmb=vec4(.25,.20725,.20724,.992);
	//vec4 matDiff=vec4(1,.829,.829,.922);
	//vec4 matSpec=vec4(.296,.296,.296,.922);
	float matShininess=11.264;
	
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
	 vec4 emission=vec4(.4,.4,.4,1);//emission+
	 //emission= vec4(0,0,0,0);
	//compute reflected color
	phongColor= emission+  .2 * matAmb + lightAmb*matAmb
			 	+ lightDiff * matDiff * max( cosTheta, 0.0 )
			 	+ lightSpec*matSpec * pow ( max(cosPhi, 0.0), matShininess); 
	//phongColor=lightDiff * matDiff * max( cosTheta, 0.0 );
	
	return phongColor;
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main()                                                                         
{   float step=.001;
	bool isoSurface=true;

	vec3 dither= vec3(rand(varyingColor.xy),rand(varyingColor.zy),rand(varyingColor.xz));
	//step+=rand(varyingColor.xy)/10000;
	mat4 invProjMat= inverse(projMatrix);
	
	//calculate the geometry
	vec2 backCoord=((varyingVert.xy/varyingVert.w+1)/2);//get the screenspace coordinates to lookup the right backface position
	vec4 outLoc=texture(backFace,backCoord);
	vec3 rayDir=outLoc.xyz-varyingColor.xyz;
	
	float rayLength=length(rayDir.xyz);

	rayDir=normalize(rayDir);
	//rayDir+=dither;
	vec3 rayStep=step*rayDir;

	float stepLength=length(rayStep);
	vec3 currentPosition=varyingColor.xyz;
	
	//currentPosition+=rayStep*dither;
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
	vec4 lookUp= texture(volume, currentPosition.xyz);

	
	for(int i=0; i<1000;i++){
		lookUp=texture(volume, currentPosition.xyz);
		scalar=lookUp.r;
		sampleColor.r=color.r/1000;
		sampleColor.g=color.g/1000;
		sampleColor.b=color.b/1000;
		
		if(scalar>thresholds.x && scalar <thresholds.y) scalar=scalar;
		else scalar=0;
		
		sampleColor.rgba=texture(transferFunction,scalar).rgba;
		sampleColor.a=1-pow((1-sampleColor.a),.002/.001);
		if(sampleColor.a>=.01){
		deltaX=(textureOffset(volume,currentPosition.xyz, off.zyy).r-textureOffset(volume,currentPosition.xyz, off.xyy).r)/2.0;
		deltaY=(textureOffset(volume,currentPosition.xyz, off.yzy).r-textureOffset(volume,currentPosition.xyz, off.yxy).r)/2.0;
		deltaZ=(textureOffset(volume,currentPosition.xyz, off.yyz).r-textureOffset(volume,currentPosition.xyz, off.yyx).r)/2.0;
		//float gradMagnitude=sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ);
		//if(gradMagnitude>.195){
		//	sampleColor.rgba=vec4(0,1,1,.2);
		//}
		
		vertexEyeSpace=invProjMat*vec4(currentPosition,1);
	
		lightColor=getPhongColor(light.position.xyz-vertexEyeSpace.xyz,vertexEyeSpace.xyz,(modelViewMatrix*vec4(deltaX,deltaY,deltaZ,0)).xyz );//(modelViewMatrix*vec4(lookUp.gba,0)).xyz    (normalMat*vec4(lookUp.gba,1)).xyz  lookUp.gba  (modelViewMatrix*vec4(deltaX,deltaY,deltaZ,0)).xyz


		currentColor.rgb+=((1-currentColor.a)*sampleColor.rgb*sampleColor.a*lightColor.rgb);//
		//currentColor.rgb+=((1-currentColor.a)*sampleColor.rgb*sampleColor.a);
		currentColor.a+=((1-currentColor.a)*sampleColor.a);//   //make sure we don't take the full alpha
		}
		//advance then check for termination
		currentPosition+=rayStep;
		currentLength+=stepLength;
		
		if(currentLength>=rayLength ||currentColor.a>=.9){
			break;
		}
		
	  

	}
	fragColor=currentColor;
                                                                         
}

