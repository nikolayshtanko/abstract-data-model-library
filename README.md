abstract-data-model-library
===========================

Abstract Data Model Library for any game project

by Nikolay Shtanko

Version: 0.1
------------


INTRODUCTION
-----------------------
The Insight Center SDK supports iOS 4.3+ for both iPad and iPhone devices. For using this SDK, you must request Consumer Key and Consumer Secret from IC team, and have Zend Desk Support Id as unique player identity. You must register schema for facebook support, request this schema from IC team too. If your build is universal, you will take two pair of consumer key-value (see using example below).

GETTING STARTED
----------------------------

###STEP 1 Adding library:
Download source code from GitHub as zip archive or clone project to local folder:
```
git clone --recursive git@github.com:Game-Insight/funzay-mobile-ios.git
```
to get repo with submodules. You can find them all in submodules/ folder.

SDK placed in FunzayMobileSDK folder. It's contents:
* libs/ - 3rd party libs, used by Funzay Mobile SDK
   * JSONKit
   * MBProgressHUD
   * WebViewJavascriptBridge
* Resources/ - different HTML pages, splashscreens, that must be inlcuded to App as resources.
* Classes/ - Funzay Mobile SDK Implementation Classes & Other Private Stuff
* FzMobile.h - Public Funzay Mobile SDK header.
* FzMobileConstants.h - Public Constants header.

Drag-n-Drop FunzayMobileSDK folder to XCode Project. Ensure to select "Copy items into destination group's folder (if needed)" & Create groups for any added folders:

![screen1](https://f.cloud.github.com/assets/1777942/1210839/1c129e46-2600-11e3-8bba-db681c126a5a.png)

###STEP 2 Facebook:
Your project must contains FacebookSDK.framework (and dependences: Social.framework, Accounts.framework, AdSupport.framework). We support both Facebook SDK version 3.x and previous ones. Version 3.x is shipped by default (as submodule). However if your game is based on previous versions you have to provide your own facebook library. In this case define FZ_MOBILE_FACEBOOK_VERSION_2 (default is FZ_MOBILE_FACEBOOK_VERSION_3) in the FacebookConfig.h and FzMobile will now work with the old version.

###STEP 3 Code modification:
####1. Create
Select place in your code, where native view elements are initialized (i.e. UIViewController viewDidLoad). Intitalize FzMobile instance (be careful, it's autoreleased object, don't lost link):
```ObjectiveC
#import "FzMobile.h"
#import "FzMobileConstants.h"
/** example for universal builds */
    NSString *consumerKey = (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) ? @"iPadKey" : @"iPhoneKey";
    NSString *consumerSecret = (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) ? @"iPadSecret" : @"iPhoneSecret";
/** init object */
    fzMobile = [FzMobile fzMobileWithConsumerKey:consumerKey consumerSecret:consumerSecret gameVersion:@"1.0" serverDomain:kFzMobileDomainMain applicationSchema:@"YourApplicationSchema" playerLevel: 1];
```
You can intitalize it several times, an each creation previous object will be destroyed automatically.

####2. Delegate
Set delegate & respond to FzMobileDelegate protocol.
```ObjectiveC
- (void)fzMobileDidHide:(FzMobile *)fzMobile
{
	/** resume your game here in case it is paused */
}

- (void)fzMobileDidShow:(FzMobile *)fzMobile
{
    /** pause your game here to release cpu resources for FzMobile */
}

- (void)fzMobileOffersDidShow:(FzMobile *)fzMobile
{
    /** you can pause your game here, if you want to stop game interaction, when offer window is shown */
}

- (void)fzMobileOffersDidHide:(FzMobile *)fzMobile
{
    /** resume your game here in case it is paused, when offer shows */
}

- (void)numberOfEventsDidChange:(FzMobile *)fzMobile numOfEvents:(NSNumber*)numOfEvents
{
	/** display the number of available events, deprecated */
}

- (void)userDataDidReceive:(FzMobile *)fzMobile onUserData:(NSString*)userData
{
	/** display the user data, when [fzMobile getUserData] was called */
}

-(void)fzMobile:(FzMobile *)fzMobile didChangeIconBadgeStringTo:(NSString *)newIconBadgeString
{
    /** deprecated, do nothing here */
}

- (void) fzMobile:(FzMobile *)fzMobile didReceiveUnknownJSMessage:(NSString *)method withParams:(NSArray *)params
{
   /* Implement it to process custom commands from server, i.e. for 
   * providing revenue for Funzay Events completition    
   * ( See "Custom FunzayServer-to-Game commands" section below for details ).
   */
    /** Example, how to implement resource delivery */
    if ([method isKindOfClass: [NSString class]] && [method isEqualToString: @"setResource"] && [params count] >= 2)
    {
        NSString *resourceType = [params objectAtIndex:0];
        NSNumber *resourceCountNumber = [params objectAtIndex: 1];
        
        if ([resourceType isKindOfClass:[NSString class]] && [resourceCountNumber isKindOfClass:[NSNumber class]])
        {
            int resourceCount = [resourceCountNumber intValue];
            
            UIAlertView * alert = [[UIAlertView alloc] initWithTitle:@"Got rewards" message:[NSString stringWithFormat:@"You got rewards %i %@", resourceCount, resourceType] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            [alert release];
        }
    } else {
        [logConsole setText:[NSString stringWithFormat:@"%@\nUnknown JS Message: %@ %@", logConsole.text, method, params]];
    }
}

- (void)fzMobile:(FzMobile *)fzMobile didReceiveJSMessage:(NSString *)message
{
    /** Helper function, you can ignore it */
}

- (BOOL)fzMobile:(FzMobile *)fzMobile shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
   /** Tell FzMobile in what orientations it can be shown. Deprecated, 
   * now it use ViewController callers, return here YES always */
    return YES;
}
```

####3. Interaction
Call start before calling show - this will start FzMobile's background processes, such as authorization, precaching content, etc. Do not call start before your game is loaded completely, otherwise some FzMobile screens can be displayed over loading screen of your game. You have to call start before tutorial.
```ObjectiveC
    [self.fzMobile start];   
```

Call this method each time your app delegate receives `-applicationWillEnterForeground:` message.
```ObjectiveC
    [self.fzMobile resume];
```

Call show with one of kFzMobileShowPageXXX constants, when you need to show Funzay Mobile dashboard.
I.e. for your FAQ button you probably will invoke this:   
```ObjectiveC
    [fzMobile show: kFzMobilePageSupportFaq];
```

Inform FzMobile about all player level & supportId changes in your game.
```ObjectiveC
    [fzMobile setPlayerLevel: self.player.level];
    [fzMobile setSupportId: self.player.supportId]; // this method must be called as soon as possible, it identify player in IC
    [fzMobile setSupportExtra:@"tesingUniqueSupportExtraData"]; // may be some serialized object data, will be attached on zend desk ticket creating
```

Inform FzMobile aboit if player is cheater and you know it.
```ObjectiveC
    [fzMobile  setIsCheater:YES]; // YES to mark as cheater, NO to unmark
```

####4. Destroy
Call -halt & set delegate to nil BEFORE releasing fzMobile instance.   
```ObjectiveC
    [self.fzMobile halt];   
    self.fzMobile.delegate = nil;
    self.fzMobile = nil; //< assume, that fzMobile is retain property, so this line will release it and set iVar to nil (recommend you to use it this way).
```
You can create new instance without halting previous, it will be done automatically. FzMobile instance worked like singleton, but on init new instance will be created, previous will be destroyed.

####5. Schema translating
Support custom URLs (This MUST be done to enable returning back to your game from Facebook app):
```ObjectiveC
- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url 
{
    if ( fzMobile )
    {
        return [fzMobile handleOpenURL:url];
    }
    return NO;
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation 
{
    if ( fzMobile )
    {
        return [fzMobile handleOpenURL:url];
    }
    return NO;
}
```
 * Add URLTypes->URL Schemes to your info.plist
![screen2](https://f.cloud.github.com/assets/1777942/1210843/24a6cb72-2600-11e3-842e-ecbe37ae8660.png)
 * Custom application shema must be provided to FzMobile instance on init/creation (*see 1. Create*).
 * The same schema with "fb12345678901234567" prefix MUST be registered for correct working of Facebook authorization    
Examples:    
`mytestapplication://` `fb12345678901234567mytestapplication://`
![screen3](https://f.cloud.github.com/assets/1777942/1210844/26aecc9e-2600-11e3-986b-bc2efecbf989.png)

####6. Purchases
Each time when user makes an internal purchase - provide this info to server by calling this method:
```Objective-C
    SKProduct *product;
    
    [self.fzMobile savePaymentInfoWithProductId:product.productIdentifier
                                       currency:[product.priceLocale objectForKey: NSLocaleCurrencyCode]
                                          price:product.price
                                subjectCurrency:@"Apple Pie"
                                  subjectAmount:[NSNumber numberWithInt: 2]];
```
 
####7. Switchers
Probably you would like to avoid any user distractions during some critical in-game moments (i.e. Bank with InAppPurchases)   
To do this - set `allowNotifications` (popup notifications) & `allowEvents` (offers) properties to `NO` when user enters bank.
But don't forget to set them back to `YES` to allow FzMobile notifications & events.
By default allowNotifications & allowEvents are enabled.    
__THIS IS IMPORTANT:__ FzMobile events uses these notifications to show user that some event is completed.   
Even more: it's possible that without showing notification user will not receive money for event completition.    
__Important:__ It is highly recommended to set allowEvents property to "No" when one of ingame windows is displayed.
This will ensure that the user will not see any offers while getting windows about level up, new game quests etc.
Just don't forget to set property to "Yes" after the in game window was closed.

####8. Portrait mode
__Portrait Mode only__ If your game/app works in portrait mode you must return 0 in supportedInterfaceOrientations for root view controller,
when Funzay window is shown (appears):
```Objective-C
- (NSUInteger)supportedInterfaceOrientations {
    if (fzMobile && fzMobile.isShown) {
        return 0;
    }
    // place your implementation here
    return UIInterfaceOrientationMaskPortrait;
}
```
Otherwise setStatusBarOrientation don't worked for UIApplication on iOS 6+, and the keyboard will be displayed incorrectly in IC while rotation.
See MainViewController in FunzayDemoApplication for example.

####9. Tokens
Asquire token for Apple remote push service and pass it to the setPushToken method in FzMobile.    
[Apple documentation about how to get token](https://developer.apple.com/library/mac/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/IPhoneOSClientImp.html)

Sample code to pass token into FzMobile:
```Objective-C
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    [fzMobile setPushToken:deviceToken];
}
```

To stat push events, sent by IC Push service, you need to call special FzMobile handlers:
```Objective-C
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    .............
    [FzMobile handleLaunchOptions:launchOptions]; // static call, not need FzMobile init
    .............
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    [FzMobile handleRemoteNotification:userInfo]; // static call, not need FzMobile init
}
```

####10. Accounts
Get current user data as JSON string.    
To receive user info override userDataDidReceive in FzMobileDelegate 
```Objective-C
- (void)userDataDidReceive:(FzMobile *)fzMobile onUserData:(NSString*)userData
{
        //NSLog([NSString stringWithFormat:  @"%@", userData]);
}
```
and call `[self.fzMobile getUserData];`    
__Result:__
  * If user isn’t authorized `{userId: null}`
  * If user is authorized  - object with user info `{userId: 42, email: "test@exmpale.com", nick: "", ...}`
  * On server error `{error: 1}`

Available fields:
  * *userId* - IC user id
  * *email* - account email
  * *nick*
  * *fbUid* - facebook uid
  * *fbName*
  * *fbFirstName*
  * *fbMiddleName*
  * *fbLastName*
  * *fbGender* - `male` or `female`
  * *fbBirthday* - YYYY-mm-dd hh:mm:ss
  
####11. Clans
If your game support clans, you must implement some calls. In FzMobileDelegate:
```Objective-C
/** Request actual player info object. If your game support clans, you must
 * return here actual data about it. Otherwise you can return null
 */
- (void) fzMobile:(FzMobile *)fzMobile getPlayerInfo:(NSMutableDictionary**)info;
{
    [*info setObject:[NSNumber numberWithLong:1234] forKey:kFzMobilePlayerInfoId];
    [*info setObject:@"PlayerName" forKey:kFzMobilePlayerInfoUsername];
    [*info setObject:[NSNumber numberWithLong:100500] forKey:kFzMobilePlayerInfoScores];
}
```

Put button in your game for opening clan window with this:
```Objective-C
    [fzMobile show: kFzMobilePageClans];
```

####12. Additionaly
To enable Funzay Mobile Logs - set logDelegate and respond to FzMobileLogDelegate protocol.
```Objective-C
    [FzMobile setLogDelegate: self];
    
    - (void)fzMobileLog:(NSString *)message withLevel:(NSInteger)logLevel
    {
        NSLog(message);
    }
```

Get IC unique device identifier (optional):
```Objective-C
    NSString *fzudid  = [self.fzMobile getUDID];
    NSLog(@"Current fzudid %@", fzudid);  // return something like '7365d4daaf6470a3eb6d5a886791b5e93e764c70'
```

__Funzay Mobile SDK isn't thread safe__
*All calls to FzMobile Classes & Objects MUST be made from main thread.*


Custom FunzayServer-to-Game commands
----------------------------------------------------------

To allow any custom server-to-game commands there's a method in FzMobileDelegate:

```Objective-C
   - (void) fzMobile:(FzMobile *)fzMobile didReceiveUnknownJSMessage:(NSString *)method withParams:(NSArray *)params;
```

Currently there's only one such command that is already used: "setResource", that have two parameters:
1. Resource type (should be NSString)
2. Resource count (should be NSNumber with int inside)

Server can send a JSON like this:

```
   {"method": "setResource", "params": ["money", 12]}
```

that will be transformed to such arguments of delegate methods:

```Objective-C
   method = @"setResource";
   params = [NSArray arrayWithObjects: @"money", [NSNumber numberWithInt: 12]];
```

You need to increment specified resource count on receiving such command.   
So you delegate method implementation can look something like this:

```Objective-C
- (void) fzMobile: (FzMobile *) fzMobile didReceiveUnknownJSMessage:(NSString *)method withParams:(NSArray *)params
{
   if ([method isKindOfClass: [NSString class]] && [method isEqualToString: @"setResource"] && [params count] >= 2)
   {
      NSString *resourceType = [params objectAtIndex:0];
      NSNumber *resourceCountNumber = [params objectAtIndex: 1];
      
      if ([resourceType isKindOfClass:[NSString class]] && [resourceCountNumber isKindOfClass:[NSNumber class]])
      {
         int resourceCount = [resourceCountNumber intValue];
         
         if ([resourceType isEqualToString:@"money"])
            self.gameController.curPlayerData.money += resourceCount;
         else if ([resourceType isEqualToString:@"wood"])
            self.gameController.curPlayerData.wood += resourceCount;
      }
   }
}
```

Insight Center GL renderer (funzay-mobile-gl add-on)
---------------------------------------------------
The Insight Center GL renderer is an add-on for FunzaySDK, LUA binding and JS-LUA bridge. It provide more optimized and additional mechanism for render offer and other windows using OpenGL. All logic are loaded from server dynamically, the game only must call some API methods on draw frame and on creating-recreating game scenes. It's written on C and fully support both iOS4+ and Android2.2+ platforms. Integration is very simple and consists of several steps. Here described only specific integration steps for iOS platform.

###STEP 1 Download source code:
There are two ways to do it. First, using git commands (if your account has permissions for funzay-mobile-gl repository):
```sh
cd funzay-mobile-ios
git submodule init
git submodule update
```
Source code will be at subdirectory submodules/funzay-mobile-gl.

Second way simply to download zip from link https://github.com/Game-Insight/funzay-mobile-gl/archive/master.zip

Note: if you clone base repository with recursive flag git clone --recursive git@github.com:Game-Insight/funzay-mobile-ios.git, it already be in submodules.

###STEP 2 Link to your project:
Drag-n-drop funzay-mobile-gl folder to XCode Project. Ensure to **unselect** "Copy items into destination group's folder (if needed)" and **select** "Create groups for any added folders". Than remove references to Android sources:
 * prebuild/android
 * src/AndroidOnly.c
 * Android.mk

###STEP 3 Modify Build Settings
Open your Build Settings and check the folllow:
```
Library Search Paths += /path/to/project/submodules/funzay-mobile-gl/prebuild/ios
Other C Flags += -DFZ_IOS
```
That's all. Then read this manual to load layer in your game scene:

https://github.com/Game-Insight/funzay-mobile-gl/blob/master/README.md








INTRODUCTION - [GL!]
-----------------------
The Insight Center GL renderer is an add-on for FunzaySDK with native calls, LUA binding and JS-LUA bridge. It provide more optimized and additional mechanism for render offer and other windows using OpenGL. All logic are loaded from server, the game only must call some API methods on draw frame and on creating-recreating game scenes. It�s written on C and fully support both iOS4+ and Android2.2+ platforms. Integration is very simple and consists of several steps.

GETTING STARTED
----------------------------
Follow platform dependent steps to include this sources to your project.

iOS - https://github.com/Game-Insight/funzay-mobile-ios#insight-center-gl-renderer-funzay-mobile-gl-add-on

Android - https://github.com/Game-Insight/funzay-mobile-android#insight-center-gl-renderer-funzay-mobile-gl-add-on

Than from your C code you must call several functions:

```C
#include "FunzayExternals.h"

print("Funzay GL renderer version %i", FZ_PLUGIN_VERSION); // here is plugin version

FzDirector_init(screen.width, screen.height); // on game scene loaded (for scenes with funzay offer windows) with screen dimensions in dp

FzDirector_draw(); // each frame rendering, skip to hide funzay for a while

FzDirector_state(FZ_STATE_PAUSE); // to pause all funzay animation

FzDirector_state(FZ_STATE_RESUME); // to resume from pause

int state = FzDirector_touch(FZ_TOUCH_BEGIN, location.x, location.y); // to send touch events, return 1 if touch was processed

FzDirector_destroy(); // on game scene unload
```

Screen dimensions and touch positions on init must be in **dp**. You can call init and destroy several times, for example if funzay is already loaded, and screen size is changes, you can call FzDirector_init() twicely. Or call FzDirector_destroy() on scene unload and on game exit without fear.

If you want to hide all funzay windows, simply skip FzDirector_draw() on draw frame. To pause or resume, send equal states FZ_STATE_PAUSE and FZ_STATE_RESUME.

FzDirector_touch() return 1 if touch event was processed and 0 otherwise. Usual touch scenario: on touch begin you ask FzDirector, if it return 1, all touch events must send to FzDirector (move, end, cancel). Later if it return 0, you can ignore other touches at all. If on begin touch FzDirector return 0, all touch events you can send in your listeners. Touch positions started from **left top corner**.

To lock funzay window appearance, call standard funzay API: ```fzMobile.allowEvents = NO;``` for iOS and ```fzView.getController().setPushesAllowed(false);``` for Android.

All methods must be called from GL thread.

UNITY EXAMPLE
-------------
Create new cs script and add it as new component to **Main Camera**
```CSharp
using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;

public class fzPlugin : MonoBehaviour {
#if UNITY_ANDROID
	private const string FUNZAY_NATIVE_MODULE = "funzay";
#else
	private const string FUNZAY_NATIVE_MODULE = "__Internal";
#endif
	
	// initialization, need be called on creation in GL thread
	[DllImport(FUNZAY_NATIVE_MODULE)]
	private static extern void FzDirector_init (int width, int height);
	
	// draw method, need be called in render circle
	[DllImport(FUNZAY_NATIVE_MODULE)]
	private static extern void FzDirector_draw ();
	
	// mouse tracking, if return 1, tap was processed
	[DllImport(FUNZAY_NATIVE_MODULE)]
	private static extern int FzDirector_touch (int touchEvent, float x, float y);

	[DllImport(FUNZAY_NATIVE_MODULE)]
	private static extern int FzDirector_touch (int touchEvent, float x, float y);

	[DllImport(FUNZAY_NATIVE_MODULE)]
	private static extern int FzDirector_state (int state);

	[DllImport(FUNZAY_NATIVE_MODULE)]
	private static extern void FzDirector_destroy ();
	
	private bool m_InTouch = false;
    
    private bool m_IsVisible = true;

	// Use this for initialization
	void Start () {

	}
    
    void Start () {
		StartCoroutine(FzStart());	
	}
	
	IEnumerator FzStart () {
		FzDirector_init (Screen.width, Screen.height);
		while (true) {
   			yield return new WaitForEndOfFrame();
			if (m_IsVisible) {
				FzDirector_draw ();
			}
		}
	}
    // global method in processin onTouch events
    public static bool UpdateInput() {
		if (m_IsVisible) {
			if (Input.touchCount > 0) {
				var touchEvent = -1;
				var touch = Input.GetTouch(0);
                switch (touch.phase) {
					case TouchPhase.Began:
						m_InTouch = true;
						touchEvent = 0;
						break;
					case TouchPhase.Moved:
						touchEvent = 1;
						break;
					case TouchPhase.Ended:
						touchEvent = 2;
						break;
					case TouchPhase.Canceled:
						touchEvent = 3;
						break;
					case TouchPhase.Stationary:
						touchEvent = 1;
						break;
					default:
                        return false;
                }
				if (m_InTouch) {
					int status = FzDirector_touch (touchEvent, touch.position.x, Screen.height - touch.position.y);
					if(status == 0) {
						m_InTouch = false;
					}
				}
			}
			return m_InTouch;
		}
		return false;
	}

	void OnDestroy() {
        m_IsVisible = false;
		FzDirector_destroy ();
	}

	void OnPauseGame() { // Some pause message
		FzDirector_state(1);
	}

	void OnResumeGame() { // Some resume message
		FzDirector_state(0);
	}
	
	bool IsInTouch () { // ability to check in other objects, is this touch processed by Funzay
		return m_InTouch;
	}
    
    void SetVisibility(bool visibility) { // ability to show / hide funzay
        m_IsVisible = visibility;
    }
}
```

COCOS2D-X EXAMPLE
-----------------
Here presented example of game object, CCNode inheritor. Simply add it to CCScene as a child.

FunzayNode.h
```C
#ifndef __FUNZAY_NODE_H__
#define __FUNZAY_NODE_H__

#include "base_nodes/CCNode.h"
#include "touch_dispatcher/CCTouchDispatcher.h"
#include "touch_dispatcher/CCTouchDelegateProtocol.h"
#include "touch_dispatcher/CCTouch.h"

#define FUNZAY_TOUCH_DELEGATE_PRIORITY 100

USING_NS_CC;

/** FunzayNode
 */
class FunzayNode: public CCNode, public CCTouchDelegate {

public:
	FunzayNode();
	virtual ~FunzayNode();
	static FunzayNode* create();
	virtual void draw();
	virtual bool init();
	virtual bool ccTouchBegan(CCTouch* touch, CCEvent* event);
	virtual void ccTouchEnded(CCTouch *touch, CCEvent* event);
	virtual void ccTouchCancelled(CCTouch *touch, CCEvent* event);
	virtual void ccTouchMoved(CCTouch* touch, CCEvent* event);
	virtual void registerWithTouchDispatcher();
	virtual void pause();
	virtual void resume();
};

#endif // __FUNZAY_NODE_H__
```
FunzayNode.cpp
```cpp
#include "FunzayNode.h"
#include "FunzayExternals.h"
#include "CCDirector.h"
#include "touch_dispatcher/CCTouchDispatcher.h"
#include "touch_dispatcher/CCTouch.h"
#include "cocoa/CCGeometry.h"

USING_NS_CC;

FunzayNode* FunzayNode::create() {
	FunzayNode* pRet = new FunzayNode();
	if (pRet && pRet->init()) {
		pRet->autorelease();
	} else {
		CC_SAFE_DELETE(pRet);
	}

	return pRet;
}

FunzayNode::FunzayNode() {
}

FunzayNode::~FunzayNode() {
	FzDirector_destroy();
}

bool FunzayNode::init() {
	CCSize size = CCDirector::sharedDirector()->getVisibleSize();
	FzDirector_init(size.width, size.height);
	registerWithTouchDispatcher();
    return true;
}

void FunzayNode::draw() {
	FzDirector_draw();
}

void FunzayNode::pause() {
	FzDirector_state(FZ_STATE_PAUSE);
}

void FunzayNode::resume() {
	FzDirector_state(FZ_STATE_RESUME);
}

void FunzayNode::registerWithTouchDispatcher() {
	CCDirector* pDirector = CCDirector::sharedDirector();
	pDirector->getTouchDispatcher()->addTargetedDelegate((CCTouchDelegate *) this, FUNZAY_TOUCH_DELEGATE_PRIORITY, true);
}

bool FunzayNode::ccTouchBegan(CCTouch* touch, CCEvent* event) {
	CC_UNUSED_PARAM(event);
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
	CCPoint location = touch->getLocationInView();
	return FzDirector_touch(FZ_TOUCH_BEGIN, location.x - origin.x, location.y - origin.y);
}

void FunzayNode::ccTouchEnded(CCTouch *touch, CCEvent* event) {
	CC_UNUSED_PARAM(event);
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
	CCPoint location = touch->getLocationInView();
	FzDirector_touch(FZ_TOUCH_END, location.x - origin.x, location.y - origin.y);
}

void FunzayNode::ccTouchCancelled(CCTouch *touch, CCEvent* event) {
	CC_UNUSED_PARAM(event);
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
	CCPoint location = touch->getLocationInView();
	FzDirector_touch(FZ_TOUCH_CANCEL, location.x - origin.x, location.y - origin.y);
}

void FunzayNode::ccTouchMoved(CCTouch* touch, CCEvent* event) {
	CC_UNUSED_PARAM(event);
	CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
	CCPoint location = touch->getLocationInView();
	FzDirector_touch(FZ_TOUCH_MOVE, location.x - origin.x, location.y - origin.y);
}
```

