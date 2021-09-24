#include "webview.h"
#ifdef WIN32
int WINAPI WinMain(HINSTANCE hInt, HINSTANCE hPrevInst, LPSTR lpCmdLine,
                   int nCmdShow) {
#else
int main() {
#endif
	webview_t w = webview_create(0, NULL);
	webview_set_title(w, "WGU Capstone");
	webview_set_size(w, 1200, 800, NULL);
	webview_navigate(w, "http://localhost:7001/game-sets/");
	webview_run(w);
	webview_destroy(w);
	return 0;
}
