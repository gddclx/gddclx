const CONFIG = {
    API_BASE_URL: 'http://localhost:8080',
    GAME_API_URL: 'http://localhost:8080/api/game'
};

function getApiBase() {
    return CONFIG.API_BASE_URL;
}

function getGameApiBase() {
    return CONFIG.GAME_API_URL;
}

function setApiBase(newUrl) {
    CONFIG.API_BASE_URL = newUrl;
    CONFIG.GAME_API_URL = newUrl + '/api/game';
}
