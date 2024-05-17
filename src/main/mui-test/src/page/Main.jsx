import {Link} from "react-router-dom";
import React from "react";
import {
    Alert, AlertTitle,
    Autocomplete,
    BottomNavigation,
    BottomNavigationAction,
    Button, Dialog, DialogContent,
    IconButton,
    Paper,
    TextField
} from "@mui/material";
import {AddShoppingCart, Alarm, Archive, Delete, Favorite, Restore, Send} from "@mui/icons-material";

const Main = () => {

    const bestEleven = [
        { label: 'Lionel Messi', id: 1 },
        { label: 'Christiano Ronaldo', id: 2 },
        { label: 'Ronaldo', id: 3 },
        { label: 'Karim Benzema', id: 4 },
        { label: 'Milan Baresi', id: 5 },
        { label: 'Pele', id: 6 },
        { label: 'Maradona', id: 7 },
        { label: 'Ji sung Park', id: 8 },
        { label: 'Sonny', id: 9 },
        { label: 'Kim min jea', id: 10 },
        { label: 'Lee Gang In', id: 11 },
    ];

    const [value, setValue] = React.useState("Recent");
    const [showAlret, setShowAlret] = React.useState(false);

    return (
        <>
            <h1>React Material UI 연습장</h1>
            <Link to="/blog">블로그 예제</Link>
            
            <h4>바텀 네비게이션 - 아래 고정 버전</h4>

            <>
                <h4>자동완성 콤보박스</h4>
                <Autocomplete
                    disablePortal
                    id="combo-box-demo"
                    options={bestEleven}
                    sx={{ width: 300 }}
                    renderInput={(params) => <TextField {...params} label="FootBall Player" variant="outlined" />}
                />
            </>
            <>
                <h4>버튼 예시</h4>
                <IconButton aria-label="delete">
                    <Delete />
                </IconButton>
                <IconButton aria-label="delete" disabled color="primary">
                    <Delete />
                </IconButton>
                <IconButton color="secondary" aria-label="add an alarm">
                    <Alarm />
                </IconButton>
                <IconButton color="primary" aria-label="add to shopping cart">
                    <AddShoppingCart />
                </IconButton>

                <Button variant="outlined" startIcon={<Delete />}>
                    Delete
                </Button>
                <Button variant="contained" style={{ backgroundColor: 'black' }} endIcon={<Send />}>
                    Send
                </Button>

                <Button
                    onClick={() => {
                        setShowAlret(true);
                    }}
                >
                    onClick Handler
                </Button>
            </>

            <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0 }} elevation={3}>
                <BottomNavigation
                    showLabels
                    value={value}
                    onChange={(event, newValue) => {
                        setValue(newValue);
                    }}
                >
                    <BottomNavigationAction label="Recents" icon={<Restore />} />
                    <BottomNavigationAction label="Favorites" icon={<Favorite />} />
                    <BottomNavigationAction label="Archive" icon={<Archive />} />
                </BottomNavigation>
            </Paper>

            {/* Dialog로 윈도우 모달처럼 쓸 수 있음 */}
            <Dialog open={showAlret} onClose={() => {setShowAlret(false)}} maxWidth="sm" fullWidth>
                <DialogContent >
                    <Alert severity="info"
                           onClose={() => {setShowAlret(false)}} style={{height: '100px'}}>
                        <AlertTitle>Info</AlertTitle>
                        This is an info Alert with an informative title.
                    </Alert>
                </DialogContent>
            </Dialog>

            <>
                <h4>Alert & AlertTitle 컴포넌트</h4>
                <Alert severity="success">
                    <AlertTitle>Success</AlertTitle>
                    This is a success Alert with an encouraging title.
                </Alert>
                <br/>
                <Alert severity="warning">
                    <AlertTitle>Warning</AlertTitle>
                    This is a warning Alert with a cautious title.
                </Alert>
                <br/>
                <Alert severity="error">
                    <AlertTitle>Error</AlertTitle>
                    This is an error Alert with a scary title.
                </Alert>
            </>
        </>
    );
}

export default Main;