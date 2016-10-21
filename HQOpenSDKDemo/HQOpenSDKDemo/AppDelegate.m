//
//  AppDelegate.m
//  HQOpenSDKDemo
//
//  Created by yao on 16/10/12.
//  Copyright © 2016年 hengqian. All rights reserved.
//

#import "AppDelegate.h"
#import "ViewController.h"
#import "HQOpenSDK.framework/Headers/HQAuthorizationManage.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor lightGrayColor];
    [self.window makeKeyAndVisible];
    
    ViewController *viewController = [[ViewController alloc] init];
    
    UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:viewController];
    self.window.rootViewController = nav;

    //初始化HQAuthorizationManage单例
    HQAuthorizationManage *manage = [HQAuthorizationManage shareHQAuthorization];
    //接入恒谦云平台时，获取的API Key
    manage.apiKey = @"fwOfy4g4GB60slS4Dh8ntE5BnhSSxgFa";
    //当前应用的urlScheme  注意：需将优e学堂的urlScheme = ExcellentLearning 加入白名单
    manage.urlScheme = @"HQOpenSDKDemo";
    
    return YES;
}


- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options
{
    //优e学堂授权登录传来的url为加密数据，需处理后，通过代理得到token secret
    [[HQAuthorizationManage shareHQAuthorization] handleApplicationUrlWithExcellentLearning:url];
    return YES;
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
