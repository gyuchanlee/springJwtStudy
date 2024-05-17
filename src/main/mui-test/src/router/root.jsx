import {lazy, Suspense} from "react";

const { createBrowserRouter } = require('react-router-dom');

const Loading = () => <>Loading...</>;
const Main = lazy(() => import('./../page/Main'));
const Blog = lazy(() => import('./../page/blog/Blog'));

const root = createBrowserRouter([
    {
        path:"",
            element:<Suspense fallback={Loading}><Main/></Suspense>
    },
    {
        path:"/blog",
        element:<Suspense fallback={Loading}><Blog/></Suspense>
    }
]);

export default root;