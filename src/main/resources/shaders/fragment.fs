#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in  vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct SpotLight
{
    PointLight pointLight;
    vec3 coneDir;
    float cutOff;
};

struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoord)
{
    if (material.hasTexture == 1)
    {
        ambientC = texture(texture_sampler, textureCoord);
        diffuseC = ambientC;
        specularC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 lightColor, float lightIntesity, vec3 position, vec3 toLightDir, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // DIFFUSE LIGHT
    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntesity * diffuseFactor;

    // SPECULAR LIGHT
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDir = -toLightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * lightIntesity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 lightDirection = light.position - position;
    vec3 toLightDir = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDir, normal);

    // APPLY ATTENUATION
    float distance = length(lightDirection);
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;

    return lightColor / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 lightDirection = light.pointLight.position - position;
    vec3 toLightDir = normalize(lightDirection);
    vec3 fromLightDir = -toLightDir;
    float spotAlpha = dot(fromLightDir, normalize(light.coneDir));

    vec4 color = vec4(0, 0, 0, 0);

    if (spotAlpha > light.cutOff)
    {
        color = calcPointLight(light.pointLight, position, normal);
        color *= (1.0 - (1.0 - spotAlpha)/(1.0 - light.cutOff));
    }
    return color;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 position, vec3 normal)
{
    return calcLightColor(directionalLight.color, directionalLight.intensity, position, normalize(directionalLight.direction), normal);
}

void main()
{
    setupColors(material, outTexCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++)
    {
        if (pointLights[i].intensity > 0)
        {
            diffuseSpecularComp += calcPointLight(pointLights[i], mvVertexPos, mvVertexNormal);
        }
    }

    for (int i=0; i < MAX_SPOT_LIGHTS; i++)
    {
        if (spotLights[i].pointLight.intensity > 0)
        {
            diffuseSpecularComp += calcSpotLight(spotLights[i], mvVertexPos, mvVertexNormal);
        }
    }

    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}