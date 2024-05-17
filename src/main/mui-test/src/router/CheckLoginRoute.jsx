import React from 'react';
import { Navigate } from 'react-router-dom';
import {useSelector} from "react-redux";

const CheckLoginRoute = ({ Component }) => {

    const memberId = useSelector(state => state.SessionInfo).memberId;

    console.log("checkLoginRoute", memberId);

    return(
        (memberId !== undefined) && (memberId !== null) && (memberId !== '') ? Component : <Navigate to="/login" {...alert("로그인이 필요합니다.")}></Navigate>
    )
}

export default CheckLoginRoute;