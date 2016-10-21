//
//  Authorization.h
//  ExcellentLearningSDK
//
//  Created by yao on 16/7/26.
//  Copyright © 2016年 yao. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger , HQAuthCheckResult)
{
    ///未设置apiKey
    HQAuthCheckLackAPIKey            = -1,
    ///未设置urlScheme
    HQAuthCheckLackURLScheme         = -2,
    ///优e学堂未安装
    HQAuthCheckAuthorAPPUnInstall    = -3,
};

FOUNDATION_EXTERN  NSString *const HQAuthDomain;

@protocol HQAuthorizationManageDelegate <NSObject>

/**
 *  解析后的accessToken和accessSecret
 *
 *  @param token
 *  @param secret
 */
- (void)hq_authorizeLoginSucessToken:(NSString *)accessToken sercet:(NSString *)accessSecret;

/**
 *  从优e学堂跳转应用，优e学堂账号与应用账号不一致时
 *  isChange : 是否选择切换账号   YES:是  NO:否
 */
- (void)hq_isChangeAccountLogin:(BOOL)isChange;


@optional

/**
 *  将要跳转到优e学堂，进行授权登录
 */
- (void)hq_authorizeLoginToLaunchExcellentLearing;

/**
 *  从优e学堂跳转应用，并且优e学堂的登录账号与当前应用账号相同
 */
- (void)hq_accountIsSameToAppAccount;

@end


@interface HQAuthorizationManage : NSObject

/** 由优e学堂开放平台颁发给第三方应用的API Key*/
@property (copy, nonatomic)NSString * apiKey;

/** 应用的scheme*/
@property (copy, nonatomic)NSString * urlScheme;

/** 代理*/
@property (strong, nonatomic)id<HQAuthorizationManageDelegate>delegate;


+ (instancetype)new UNAVAILABLE_ATTRIBUTE;
- (instancetype)init UNAVAILABLE_ATTRIBUTE;
/** 获得单例对象*/
+ (instancetype)shareHQAuthorization;


/**
 *  开始授权登录方式检测
 *
 *  @param urlScheme 你app的URL Scheme
 *  err 授权检测错误原因
 */
- (void)authorizeLoginBegin:(NSError * __autoreleasing*)err;


/**
 *  处理优e学堂传递的url加密数据
 *  @param url 优e学堂跳转回来的url
 */
- (void)handleApplicationUrlWithExcellentLearning:(NSURL *)url;


/**
 *  获取签名sign 或 get请求完整的url地址
 *  @param urlStr 请求的url
 *  @param parmaDid 请求用到的参数 （参数除sign外都需要传，可以传空）
 *  @param secretKey 若该接口请求参数中有token，secretKey为授权登录返回的secret
                     否则secretKey为在恒谦云平台创建应用时，获取的Secret Key
 *  @param completeUrl YES:返回get请求完整的url NO:返回签名sign
 */
- (NSString *)getHQAuthSignWithURL:(NSString *)urlStr withParma:(NSDictionary *)parmaDic withSecretKey:(NSString *)secretKey  completeUrl:(BOOL)completeUrl;


/**
 *  退出账号时，清理sdk相关信息
 */
- (void)resetExcellentLearningSDKCacheProfile;


@end

