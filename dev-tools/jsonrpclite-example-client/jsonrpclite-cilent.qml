import QtQuick 2.8
import QtQuick.Window 2.2

import QtWebSockets 1.0
import QtQuick.Controls 2.2
import QtQuick.Layouts 1.3

ApplicationWindow {
    width: 360
    height: 680
    visible: true
    title: qsTr("dapp-bet")

    header: ToolBar {
        RowLayout {
            anchors.fill: parent
            ToolButton {
                text: "connect"
                onClicked: {
                    socket.active = !socket.active;
                }
            }
        }
    }

    ColumnLayout {
        anchors.fill: parent
        anchors.margins: 10

        Button {
            Layout.fillWidth: true
            text: "pub.ping"
            onClicked: {
                pubPing(function(res){
                    console.log("pubPing:" + JSON.stringify(res));
                });
            }
        }

        Button {
            Layout.fillWidth: true
            text: "pub.sub.tick"
            onClicked: {
                pubSubTick(function(res){
                    console.log("pubSubTick:" + JSON.stringify(res));
                });
            }
        }

        Item {
            Layout.fillHeight: true
        }
    }

    function pubSubTick(callback) {
        subChannel("pub.sub.tick", [], true, callback);
    }

    function pubPing(callback) {
        callRpcMethod("pub.ping", [], callback);
    }

    function callRpcMethod(method, params, callback) {
        var id = (new Date()).getTime();

        var req = {
            id: id,
            method: method,
            params: params
        }

        callback = callback || function(res) {
            console.log(JSON.stringify(res));
        };

        rpcCallback[id] = (function(response){
            callback(response);
        });

        socket.sendTextMessage(JSON.stringify(req));
    }

    function subChannel(channel, params, subscribe, callback) {
        subscribe = subscribe || true;
        callback = callback || function(res) {
            console.log(JSON.stringify(res));
        };

        var req = {
            channel: channel,
            params: params,
            subscribe: subscribe
        }

        channelCallback[channel] = callback;

        socket.sendTextMessage(JSON.stringify(req));
    }


    readonly property var rpcCallback:({});
    readonly property var channelCallback:({});

    WebSocket {
        id: socket
        url: "ws://localhost:8080/game"
        onTextMessageReceived: {

            var obj = JSON.parse(message);

            if (typeof obj.channel !== 'undefined') {
                var channelCB = channelCallback[obj.channel];
                if (typeof channelCB !== 'undefined') {
                    channelCB(obj);
                } else {
                    console.error("have not channel:" + obj.channel + " callback")
                }
            }

            if (typeof obj.id !== 'undefined') {
                var rpcCB = rpcCallback[obj.id];
                if (typeof rpcCB !== 'undefined') {
                    rpcCB(obj);

                    delete rpcCallback[obj.id];

                } else {
                    console.error("have not id:" + obj.id + " callback, method: "  + obj.method)
                }
            }
        }
    }
}
