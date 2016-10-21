//
//  ViewController.m
//  HQOpenSDKDemo
//
//  Created by yao on 16/10/12.
//  Copyright © 2016年 hengqian. All rights reserved.
//

#import "ViewController.h"
#import "HQOpenSDK.framework/Headers/HQAuthorizationManage.h"

@interface ViewController ()<HQAuthorizationManageDelegate,NSURLSessionDataDelegate>

@property (nonatomic , copy) NSString *secret;
@property (nonatomic , copy) NSString *token;

@property (weak, nonatomic) IBOutlet UITextView *textView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.navigationItem.title = @"优e学堂授权登录Demo";
    self.navigationController.navigationBar.barTintColor = [UIColor grayColor];
}


#pragma mark - touchButton

//授权登录
- (IBAction)touchAuthButton:(id)sender {
    
    self.navigationItem.title = @"授权登录";
    
    HQAuthorizationManage *manage = [HQAuthorizationManage shareHQAuthorization];
    manage.delegate = self;
    NSError *err = nil;
    [manage authorizeLoginBegin:&err];
    
    //授权登录失败错误原因
    if (err) {
        NSLog(@"err.localizedDescription = %@",err.localizedDescription);
        NSLog(@"err.localizedFailureReason = %@",err.localizedFailureReason);
        NSLog(@"err.code = %zd",err.code);
    }
}


//获取当前登录用户的资料。
- (IBAction)touchGetUserInfoMessageButton:(id)sender {
    
    self.navigationItem.title = @"当前登录用户的资料";
    
    [self.textView setText:@""];
    
    if (self.token.length == 0 || self.secret.length == 0) {
        [self.textView setText:@"需先进行授权登录，获取token，secret"];
        return;
    }
    HQAuthorizationManage *manage = [HQAuthorizationManage shareHQAuthorization];
    
    NSDictionary *dic = @{@"consumerKey":manage.apiKey,
                          @"timeTamp":[self getCurrentSystemDateSecond],
                          @"token":self.token,
                          @"field":@""};
    
    /*
     //若completeUrl参数传NO 则返回请求参数需要的签名sign，需自己再拼接url
     NSString *sign = [[HQAuthorizationManage shareHQAuthorization] getHQAuthSignWithURL:@" http://api.hengqian.net/openApi/users/me.json" withParma:dic withSecretKey:self.secret completeUrl:NO];
     */
    
    //此接口请求参数中有token，故SecretKey为授权登录时返回的secret
    NSString *urlStr = [manage getHQAuthSignWithURL:@"http://api.hengqian.net/openApi/users/me.json"
                                          withParma:dic
                                      withSecretKey:self.secret
                                        completeUrl:YES];
    //get请求
    [self requestInfo:urlStr];
}



//获取某个班级的信息。
- (IBAction)touchGetClassMessageButton:(id)sender {
    
    self.navigationItem.title = @"某个班级的信息";
    [self.textView setText:@""];
    
    HQAuthorizationManage *manage = [HQAuthorizationManage shareHQAuthorization];
    
    //此接口请求参数中没有token，故SecretKey为接入恒谦云平台时，获取的Secret Key
    NSString *secretKey = @"37b79d4eccbe86d5464f17f6b09cd83a";
    
    NSDictionary *dic = @{@"consumerKey":manage.apiKey,
                          @"timeTamp":[self getCurrentSystemDateSecond],
                          @"cid":@"fe4aace0-fbab-43d3-bac6-93acc6c6b5c2",
                          @"field":@"member"};
    
    NSString *urlStr = [manage getHQAuthSignWithURL:@"http://api.hengqian.net/openapi/classes/show.json"
                                          withParma:dic
                                      withSecretKey:secretKey
                                        completeUrl:YES];
    //get请求
    [self requestInfo:urlStr];
}


//退出时
- (IBAction)touchExitButton:(id)sender {
    [self.textView setText:@"清除sdk缓存文件"];
    self.secret = nil;
    self.token = nil;
    //清除sdk缓存的文件
    [[HQAuthorizationManage shareHQAuthorization] resetExcellentLearningSDKCacheProfile];
}


#pragma mark - request

- (void)requestInfo:(NSString *)urlStr{
    
    NSURL *url = [NSURL URLWithString:urlStr];
    NSURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    ;
    NSURLSessionDataTask *task = [[NSURLSession sharedSession] dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        
        if (data)
        {
            NSDictionary *jsonDic= [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:nil];
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.textView setText:jsonDic.description];
            });
        }
        
    }];
    [task resume];
}



#pragma mark - 得到当前的系统UINX时间戳(秒)

// 得到当前的系统UINX时间戳(秒)
- (NSString *)getCurrentSystemDateSecond
{
    // 得到当前的系统时间(秒)
    NSDate *currentDate = [[NSDate alloc] init];
    NSTimeInterval timeInterval = [currentDate timeIntervalSince1970];
    NSString *strTimeInterval = [NSString stringWithFormat:@"%zd", (long long)timeInterval];
    
    long long timeSecond = [strTimeInterval longLongValue];
    
    return [NSString stringWithFormat:@"%lld",timeSecond];
}


#pragma mark - HQAuthorizationManageDelegate

/**
 *  跳转优e学堂授权登录成功
 *
 *  @param accessToken
 *  @param accessSecret
 */
- (void)hq_authorizeLoginSucessToken:(NSString *)accessToken sercet:(NSString *)accessSecret
{
    self.secret = accessSecret;
    self.token = accessToken;
    NSString *text = [NSString stringWithFormat:@"授权登录成功返回值：\n token = %@ \n secret = %@",self.token,self.secret];
    [self.textView setText:text];
}


/**
 *  从优e学堂跳转应用，优e学堂账号与应用账号不一致时
 *  isChange : 是否选择切换账号   YES:是  NO:否
 */
- (void)hq_isChangeAccountLogin:(BOOL)isChange
{
    if (isChange) {
        //用户选择切换账号
        [self.textView setText:@"切换账号"];
        
    }else
    {
        //不切换账号
        [self.textView setText:@"不切换账号"];
    }
}


/**
 *  将要跳转到优e学堂，进行授权登录
 */
- (void)hq_authorizeLoginToLaunchExcellentLearing
{
    NSLog(@"将要跳转到优e学堂，进行授权登录");
}


/**
 *  从优e学堂跳转到应用，并且优e学堂的登录账号与当前应用账号相同
 */
- (void)hq_accountIsSameToAppAccount
{
    [self.textView setText:@"从优e学堂跳转到应用，并且优e学堂的登录账号与当前应用账号相同"];
}


#pragma mark - --

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
