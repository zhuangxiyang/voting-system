package com.vote.common.swagger;

import com.google.common.collect.Lists;
import com.vote.common.util.Constant;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * API文档配置
 *
 *
 */
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket createRestApi(){
		ParameterBuilder builder = new ParameterBuilder();
		builder.parameterType("header").name(Constant.AUTHORIZATION)
		.description("header参数")
		.required(false)
		.modelRef(new ModelRef("string")); // 在swagger里显示header
		return new Docket(DocumentationType.SWAGGER_2).groupName("vote--接口文档")
				.apiInfo(new ApiInfoBuilder().title("vote--接口文档")
						.contact(new Contact("vote", "", "vote-admin@cxd-logistics.com")).version("1.0").build())
				.globalOperationParameters(Lists.newArrayList(builder.build()))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.vote"))
				.paths(PathSelectors.any()).build();
	}
}