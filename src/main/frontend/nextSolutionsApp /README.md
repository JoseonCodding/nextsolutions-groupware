# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

```
frontend
├─ README.md
├─ eslint.config.js
├─ index.html
├─ package-lock.json
├─ package.json
├─ public
│  └─ vite.svg
├─ src
│  ├─ App.jsx
│  ├─ api
│  │  └─ axiosApi.js
│  ├─ assets
│  │  ├─ fonts
│  │  │  ├─ NotoSans-Black.eot
│  │  │  ├─ NotoSans-Black.otf
│  │  │  ├─ NotoSans-Black.woff
│  │  │  ├─ NotoSans-Black.woff2
│  │  │  ├─ NotoSans-Bold.eot
│  │  │  ├─ NotoSans-Bold.otf
│  │  │  ├─ NotoSans-Bold.woff
│  │  │  ├─ NotoSans-Bold.woff2
│  │  │  ├─ NotoSans-DemiLight.eot
│  │  │  ├─ NotoSans-DemiLight.otf
│  │  │  ├─ NotoSans-DemiLight.woff
│  │  │  ├─ NotoSans-DemiLight.woff2
│  │  │  ├─ NotoSans-Light.eot
│  │  │  ├─ NotoSans-Light.otf
│  │  │  ├─ NotoSans-Light.woff
│  │  │  ├─ NotoSans-Light.woff2
│  │  │  ├─ NotoSans-Medium.eot
│  │  │  ├─ NotoSans-Medium.otf
│  │  │  ├─ NotoSans-Medium.woff
│  │  │  ├─ NotoSans-Medium.woff2
│  │  │  ├─ NotoSans-Regular.eot
│  │  │  ├─ NotoSans-Regular.otf
│  │  │  ├─ NotoSans-Regular.woff
│  │  │  ├─ NotoSans-Regular.woff2
│  │  │  ├─ NotoSans-Thin.eot
│  │  │  ├─ NotoSans-Thin.otf
│  │  │  ├─ NotoSans-Thin.woff
│  │  │  └─ NotoSans-Thin.woff2
│  │  ├─ images
│  │  └─ react.svg
│  ├─ components
│  │  ├─ approval
│  │  ├─ attendance
│  │  ├─ board
│  │  ├─ common
│  │  │  └─ Logo.jsx
│  │  ├─ menus
│  │  │  ├─ BasicMenu copy.jsx
│  │  │  └─ BasicMenu.jsx
│  │  ├─ project
│  │  │  └─ Project.jsx
│  │  └─ schedule
│  │     └─ Calendar.jsx
│  ├─ hooks
│  ├─ layouts
│  │  └─ BasicLayout.jsx
│  ├─ lib
│  ├─ main.jsx
│  ├─ pages
│  │  ├─ MainPage.jsx
│  │  ├─ approval
│  │  │  ├─ IndexPage.jsx
│  │  │  ├─ ListPage.jsx
│  │  │  └─ ReadPage.jsx
│  │  ├─ attendance
│  │  │  ├─ IndexPage.jsx
│  │  │  ├─ commute
│  │  │  │  ├─ AddPage.jsx
│  │  │  │  ├─ CalendarPage.jsx
│  │  │  │  └─ ListPage.jsx
│  │  │  └─ leave
│  │  │     ├─ AddPage.jsx
│  │  │     ├─ EditPage.jsx
│  │  │     └─ ListPage.jsx
│  │  ├─ auth
│  │  │  ├─ login
│  │  │  │  └─ LoginPage.jsx
│  │  │  └─ signup
│  │  │     └─ SignupPage.jsx
│  │  ├─ board
│  │  │  ├─ IndexPage.jsx
│  │  │  ├─ admin
│  │  │  │  ├─ AddPage.jsx
│  │  │  │  ├─ EditPage.jsx
│  │  │  │  └─ ListPage.jsx
│  │  │  ├─ free
│  │  │  │  ├─ AddPage.jsx
│  │  │  │  ├─ EditPage.jsx
│  │  │  │  ├─ ListPage.jsx
│  │  │  │  └─ ReadPage.jsx
│  │  │  └─ notice
│  │  │     ├─ AddPage.jsx
│  │  │     ├─ EditPage.jsx
│  │  │     ├─ ListPage.jsx
│  │  │     └─ ReadPage.jsx
│  │  ├─ document
│  │  │  ├─ IndexPage.jsx
│  │  │  ├─ ListPage.jsx
│  │  │  └─ ReadPage.jsx
│  │  ├─ member
│  │  │  ├─ AddPage.jsx
│  │  │  ├─ EditPage.jsx
│  │  │  ├─ IndexPage.jsx
│  │  │  └─ ListPage.jsx
│  │  ├─ project
│  │  │  ├─ AddPage.jsx
│  │  │  ├─ EditPage.jsx
│  │  │  ├─ IndexPage.jsx
│  │  │  ├─ ListPage.jsx
│  │  │  └─ ReadPage.jsx
│  │  └─ schedule
│  │     ├─ AddPage.jsx
│  │     ├─ CalendarPage.jsx
│  │     ├─ EditPage.jsx
│  │     └─ IndexPage.jsx
│  ├─ router
│  │  ├─ approvalRouter.jsx
│  │  ├─ attendanceRouter.jsx
│  │  ├─ boardRouter.jsx
│  │  ├─ documentRouter.jsx
│  │  ├─ memberRouter.jsx
│  │  ├─ projectRouter.jsx
│  │  ├─ root.jsx
│  │  └─ scheduleRouter.jsx
│  ├─ slices
│  │  └─ loginSlice.js
│  ├─ store.js
│  └─ styles
│     ├─ App.css
│     ├─ components
│     ├─ index.css
│     └─ noto-sans-korean.css
└─ vite.config.js

```